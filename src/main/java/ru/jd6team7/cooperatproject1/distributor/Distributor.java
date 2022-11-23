package ru.jd6team7.cooperatproject1.distributor;

import org.springframework.stereotype.Component;

/**Просто родитель для распределялок. Не знаю, зачем - но красиво.
 */
@Component
public abstract class Distributor {
    public abstract void getDistribute(long chatId, String message);
}
