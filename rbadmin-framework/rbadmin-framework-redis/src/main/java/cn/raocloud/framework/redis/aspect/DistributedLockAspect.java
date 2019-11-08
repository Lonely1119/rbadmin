package cn.raocloud.framework.redis.aspect;

import cn.raocloud.framework.redis.annotation.DistributedLock;
import cn.raocloud.framework.redis.lock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @ClassName: DistributedLockAspect
 * @Description: TODO 分布式锁切面，通过切面编程对需要加锁的方法添加统一加锁行为，减少对业务逻辑代码的侵入
 * @Author: raobin
 * @Date: 2019/8/15 10:02
 */
@Slf4j
@Aspect
public class DistributedLockAspect {

    @Autowired
    private RedisDistributedLock redisDistributedLock;

    /**
     * 扫描所有被@DistributedLock注解标识的方法
     * @param joinPoint
     * @return
     */
    @Around("@annotation(cn.raocloud.framework.redis.annotation.DistributedLock)")
    public Object lockAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ExpressionParser parser = new SpelExpressionParser();
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        EvaluationContext context = new StandardEvaluationContext();

        // 获取方法对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取方法上DistributedLock注解
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        // 获取方法参数名称和参数值
        String[] paramNames = discoverer.getParameterNames(method);
        Object[] paramValues = joinPoint.getArgs();
        if(paramNames != null && paramValues != null ){
            for(int i = 0; i < paramNames.length; i++){
                context.setVariable(paramNames[i], paramValues[i]);
            }
        }

        // 获取注解相关属性值
        // 解析spel表达式，获取对应参数值
        String lockKey = annotation.lockKey();
        if(lockKey.length() > 0){
            try {
                Expression expression = parser.parseExpression(lockKey);
                lockKey = expression.getValue(context, String.class);
            } catch (ParseException | SpelEvaluationException e) {
                if(e instanceof SpelEvaluationException){
                    try {
                        // 判断是否不需要进行spel表达式进行解析，即字面量
                        String temp = "'" + lockKey + "'";
                        Expression expression = parser.parseExpression(temp);
                        lockKey = expression.getValue(context, String.class);
                    } catch (Exception e1) {
                        if(log.isErrorEnabled()){
                            log.error(e.getMessage() + "{" + signature.toString() + " [lockKey]字段spel表达式计算失败" + "}", e.getCause());
                        }
                        throw e1;
                    }
                } else {
                    // spel表达式解析失败
                    if(log.isErrorEnabled()){
                        log.error(e.getMessage() + "{" + signature.toString() + " [lockKey]字段值spel表达式解析失败" + "}", e.getCause());
                    }
                    throw e;
                }
            }
        }
        String lockPrefix = annotation.lockPrefix();
        long lockTime = annotation.lockTime() > 1 ? annotation.lockTime() : 1;
        long waitTime = annotation.waitTime() > 1 ? annotation.waitTime() : 1;

        // 为当前线程获取的锁生成唯一标识，防止删除其他线程获取的锁
        String uniqueIdentifier = UUID.randomUUID().toString();

        // 获取分布式锁开始时间
        long startTimeMillis = System.currentTimeMillis();
        // 未获取到分布式锁时，等待截止时间，超过等待截止时间则放弃获取分布式锁
        long endTimeMillis = startTimeMillis + waitTime;
        // 获取到分布式锁的时间
        long acquireTimeMillis = startTimeMillis;
        // 借鉴JDK的自旋锁思想->获取分布式锁失败后，通过等待一段时间(根据业务决定)再次去获取分布式锁，减少对CPU的占用率
        // 后期可以进行优化成自适应自旋锁方式，保存前N获取到锁时消耗的时间，取中位值决定等待时间长短
        try {
            do {
                if(redisDistributedLock.acquireLock(lockPrefix, lockKey, uniqueIdentifier, lockTime)){
                    acquireTimeMillis = System.currentTimeMillis();
                    if(log.isDebugEnabled()) {
                        log.debug("获取锁 {}:{} 成功,方法名为{},获取到锁时间为{}ms", lockPrefix, lockKey,
                                joinPoint.getSignature(), acquireTimeMillis - startTimeMillis);
                    }
                    // 获取分布式锁后开启一个守护线程进行续租处理
                    ExpandProcessor expandProcessor = new ExpandProcessor(lockPrefix, lockKey, uniqueIdentifier, lockTime);
                    Thread expandThread = new Thread(expandProcessor);
                    expandThread.setDaemon(true);
                    expandThread.start();
                    // 指定业务逻辑
                    Object resultObj = joinPoint.proceed(paramValues);
                    // 设置守护线程关闭标志，通过interrupt终端sleep状态，保证线程及时销毁
                    expandProcessor.stop();
                    expandThread.interrupt();
                    return resultObj;
                }
                // 获取分布式锁失败，等待一段时间再次去获取，防止一直占用CPU
                long sleepTime = Math.min(100, waitTime);
                if(log.isDebugEnabled()){
                    log.debug("无法获得锁 {}:{},重新尝试获取锁等待时间为{}ms,方法名为{}", lockPrefix, lockKey, sleepTime, joinPoint.getSignature());
                }
                Thread.sleep(sleepTime);
            } while(System.currentTimeMillis() <= endTimeMillis);
            if(log.isDebugEnabled()){
                log.debug("获得锁 {}:{} 失败,放弃等待,方法名为{}, 等待时间为{}ms", lockPrefix, lockKey,
                        joinPoint.getSignature(), System.currentTimeMillis() - startTimeMillis);
            }
            return null;
        } finally {
            if(redisDistributedLock.releaseLock(lockPrefix, lockKey, uniqueIdentifier)){
                if(log.isDebugEnabled()) {
                    log.debug("释放锁 {}:{} 成功,方法名为{},持有锁时间为{}ms", lockPrefix, lockKey,
                            joinPoint.getSignature(), System.currentTimeMillis() - acquireTimeMillis);
                }
            }
        }
    }

    /**
     * 过期时间续租处理器
     *  1、和释放锁的情况一样，需要先判断所对象是否改变，否则会造成无论谁持有锁，守护线程都会去重新设置锁的过期时间
     *  2、守护线程要在合理的时间去重新设置锁的过期时间，否则造成资源浪费
     *  3、持有锁的线程已经处理完业务，守护线程也应该被销毁
     */
    class ExpandProcessor implements Runnable{

        private String prefix;

        private String key;

        private String value;

        private long milliseconds;

        ExpandProcessor(String prefix, String key, String value, long milliseconds) {
            this.prefix = prefix;
            this.key = key;
            this.value = value;
            this.milliseconds = milliseconds;
        }

        // 线程关闭标记
        private volatile boolean signal = true;
        void stop(){
            this.signal = false;
        }

        @Override
        public void run() {
            long waitTime = milliseconds * 2 / 3;
            while(signal){
                try {
                    Thread.sleep(waitTime);
                    if(redisDistributedLock.expandLockTime(prefix, key, value, milliseconds)){
                        if (log.isDebugEnabled()) {
                            log.debug("锁 {}:{} 过期时间续租成功,等待时间为{}ms,将重置锁超时时间重置为{}ms", prefix, key, waitTime, milliseconds);
                        }
                    } else {
                        // 防止执行业务逻辑时抛出异常，不会执行@Around的停止线程和中断线程操作，导致守护线程没有关闭
                        // 从@Around代码中可以看到不管业务逻辑是否会抛出异常都会执行释放锁操作
                        // 通过这一特性可以在守护线程进行续租的时候，判断是否续租成功，续租失败守护线程将停止工作
                        if (log.isInfoEnabled()){
                            log.info("锁 {}:{} 过期时间续租失败，ExpandProcessor续租处理线程将中断", prefix, key);
                        }
                        this.stop();
                    }
                } catch (InterruptedException e){
                    if (log.isDebugEnabled()) {
                        log.debug("ExpandProcessor续租处理线程被强制中断");
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("ExpandProcessor续租处理线程已停止");
            }
        }
    }
}
