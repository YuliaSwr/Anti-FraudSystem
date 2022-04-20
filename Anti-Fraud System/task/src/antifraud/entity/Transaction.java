package antifraud.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;

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
}
