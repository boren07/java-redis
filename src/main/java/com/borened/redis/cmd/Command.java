package com.borened.redis.cmd;

import lombok.Data;

/**
 * 命令描述
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class Command {

    private String cmd;

    private String pattern;

}
