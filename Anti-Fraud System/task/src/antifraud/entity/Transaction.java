package antifraud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {

    @SequenceGenerator(
            name = "trans_sequence",
            sequenceName = "trans_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "trans_sequence"
    )
    @JsonIgnore
    private Long id;

    @NumberFormat
    private Long amount;

    public Long getAmount() {
        if (amount==null) {
            return 0L;
        }
        return amount;
    }

    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!$)|$)){4}$")
    private String ip;

    private String number;

    @Enumerated(value = EnumType.STRING)
    private Region region;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
}
