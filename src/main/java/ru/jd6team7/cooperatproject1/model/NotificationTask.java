package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @Column(name = "telegram_chat_id")
    private long telegramChatID;
    @Column(name = "message_text")
    private String messageText;
    @Column(name = "message_datetime")
    private LocalDateTime messageDateTime;
    @Column(name = "send_datetime")
    private LocalDateTime sendDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return this.getTelegramChatID() == that.getTelegramChatID() && Objects.equals(this.getMessageText(), that.getMessageText()) && Objects.equals(this.getMessageDateTime(), that.getMessageDateTime()) && Objects.equals(this.getSendDateTime(), that.getSendDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTelegramChatID(), this.getMessageText(), this.getMessageDateTime(), this.getSendDateTime());
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", telegram_chat_id=" + telegramChatID +
                ", message_text='" + messageText + '\'' +
                ", message_datetime=" + messageDateTime +
                ", send_datetime=" + sendDateTime +
                '}';
    }
}
