package org.example.repository;

import org.example.Account;
import org.example.Enum.AccountClEnum;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountRepositoryTest {

    private final AccountRepository repository = new AccountRepository();

    @Test
    void 같은_코드의_계정을_두_번_저장할_수_없다() {
        repository.save(new Account("001", "현금", AccountClEnum.ASSET));

        assertThrows(IllegalArgumentException.class,
                () -> repository.save(new Account("001", "예금", AccountClEnum.ASSET)));
    }
}
