package clf.integra.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.io.Serializable;
import java.util.UUID;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Notification implements Serializable {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(updatable = false, nullable = false)
    UUID id;

    @ManyToOne
    User user;

    NotificationType type;

    String message;
}
