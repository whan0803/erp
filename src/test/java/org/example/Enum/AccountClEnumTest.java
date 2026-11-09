package org.example.Enum;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountClEnumTest {

    @ParameterizedTest
    @CsvSource({
            "ASSET, 자산",
            "LIABILITY, 부채",
            "REVENUE, 수익",
            "EXPENSE, 비용"
    })
    void 각_분류의_한글_이름을_반환한다(AccountClEnum category, String label) {
        assertEquals(label, category.getLabel());
    }

    @ParameterizedTest
    @CsvSource({
            "자산, ASSET",
            "부채, LIABILITY",
            "수익, REVENUE",
            "비용, EXPENSE"
    })
    void 한글_이름으로_계정_분류를_찾는다(String label, AccountClEnum expected) {
        assertEquals(expected, AccountClEnum.fromLabel(label));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"자본", "현금", "ASSET"})
    void 허용되지_않는_분류_이름은_거절한다(String label) {
        assertThrows(IllegalArgumentException.class,
                () -> AccountClEnum.fromLabel(label));
    }
}