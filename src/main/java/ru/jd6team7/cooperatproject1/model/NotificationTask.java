package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

/** Сущность "Задание уведомления" хранит реестр сообщений,
 * отправляемых в чаты Опекунам при задолженности по ежедневным отчётам.
 * Одновременно служит хранение сообщений для чатов с Волонтёрами, для уведомления их
 * о желании Посетителя выйти на контакт (позвать Волонтёра),
 * либо в ситуации, когда Опекун не предоставляет длительное время отчёты.
 * */
@Entity
@Getter
@Setter
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long telegram_chat_id;
    private String message_text;
    private LocalDateTime message_datetime;
    private LocalDateTime send_datetime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return getTelegram_chat_id() == that.getTelegram_chat_id() && Objects.equals(getMessage_text(), that.getMessage_text()) && Objects.equals(getMessage_datetime(), that.getMessage_datetime()) && Objects.equals(getSend_datetime(), that.getSend_datetime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTelegram_chat_id(), getMessage_text(), getMessage_datetime(), getSend_datetime());
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", telegram_chat_id=" + telegram_chat_id +
                ", message_text='" + message_text + '\'' +
                ", message_datetime=" + message_datetime +
                ", send_datetime=" + send_datetime +
                '}';
    }
}
