package antifraud.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Pattern;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

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
}
