package cn.raocloud.framework.tool.filter;

import org.springframework.util.Assert;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @ClassName: SuffixFileFilter
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 16:50
 * @Version 1.0
 */
public class SuffixFileFilter implements FileFilter, Serializable {
    private static final long serialVersionUID = -6775754846294963752L;

    private final String[] suffixes;

    public SuffixFileFilter(final String suffix){
        Assert.notNull(suffix, "The suffix must not be null");
        this.suffixes = new String[]{suffix};
    }

    public SuffixFileFilter(final String[] suffixes){
        Assert.notNull(suffixes, "The suffixes must not be null");
        this.suffixes = Arrays.copyOf(suffixes, suffixes.length);
    }

    @Override
    public boolean accept(File pathname) {
        final String filename = pathname.getName();
        for(final String suffix : suffixes){
            if(filename.endsWith(suffix)){
                return true;
            }
        }
        return false;
    }

}
