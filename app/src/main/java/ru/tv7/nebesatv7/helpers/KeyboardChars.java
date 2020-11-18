package ru.tv7.nebesatv7.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.tv7.nebesatv7.R;

public class KeyboardChars {
    public static final List<SearchCharacter> SEARCH_CHARACTER_ROW_1 = new ArrayList<>(
        Arrays.asList(
            new SearchCharacter(R.id.k_0_0,'ё', 'Ё','1'),
            new SearchCharacter(R.id.k_0_1,'ъ', 'Ъ','2'),
            new SearchCharacter(R.id.k_0_2,'я', 'Я','3'),
            new SearchCharacter(R.id.k_0_3,'ш', 'Ш','4'),
            new SearchCharacter(R.id.k_0_4,'е', 'Е','5'),
            new SearchCharacter(R.id.k_0_5,'р', 'Р','6'),
            new SearchCharacter(R.id.k_0_6,'т', 'Т','7'),
            new SearchCharacter(R.id.k_0_7,'ы', 'Ы','8'),
            new SearchCharacter(R.id.k_0_8,'у', 'У','9'),
            new SearchCharacter(R.id.k_0_9,'и', 'И','0'),
            new SearchCharacter(R.id.k_0_10,'о', 'О',','),
            new SearchCharacter(R.id.k_0_11,'п', 'П',null),
            new SearchCharacter(R.id.k_0_12,'ю', 'Ю',null)
        )
    );

    public static final List<SearchCharacter> SEARCH_CHARACTER_ROW_2 = new ArrayList<>(
        Arrays.asList(
            new SearchCharacter(R.id.k_1_0,'щ', 'Щ', '.'),
            new SearchCharacter(R.id.k_1_1,'э', 'Э', ';'),
            new SearchCharacter(R.id.k_1_2,'а', 'А', ':'),
            new SearchCharacter(R.id.k_1_3,'с', 'С', '!'),
            new SearchCharacter(R.id.k_1_4,'д', 'Д', '='),
            new SearchCharacter(R.id.k_1_5,'ф', 'Ф', '/'),
            new SearchCharacter(R.id.k_1_6,'г', 'Г', '('),
            new SearchCharacter(R.id.k_1_7,'ч', 'Ч', ')'),
            new SearchCharacter(R.id.k_1_8,'й', 'Й', '['),
            new SearchCharacter(R.id.k_1_9,'к', 'К', ']'),
            new SearchCharacter(R.id.k_1_10,'л', 'Л', '-'),
            new SearchCharacter(R.id.k_1_11,'ь', 'Ь', null)
        )
    );

    public static final List<SearchCharacter> SEARCH_CHARACTER_ROW_3 = new ArrayList<>(
        Arrays.asList(
            new SearchCharacter(R.id.k_2_0,'ж', 'Ж', '_'),
            new SearchCharacter(R.id.k_2_1,'з', 'З', '*'),
            new SearchCharacter(R.id.k_2_2,'х', 'Х', '>'),
            new SearchCharacter(R.id.k_2_3,'ц', 'Ц', '<'),
            new SearchCharacter(R.id.k_2_4,'в', 'В', '#'),
            new SearchCharacter(R.id.k_2_5,'б', 'Б', '?'),
            new SearchCharacter(R.id.k_2_6,'н', 'Н', '+'),
            new SearchCharacter(R.id.k_2_7,'м', 'М', '&'),
            new SearchCharacter(R.id.k_2_8,null, null, null),
            new SearchCharacter(R.id.k_2_9,null, null, null),
            new SearchCharacter(R.id.k_2_10,null, null, null)
        )
    );

    public static class SearchCharacter {
        private int id = 0;
        private Character lowercase = null;
        private Character uppercase = null;
        private Character special = null;

        public SearchCharacter(int id, Character lowercase, Character uppercase, Character special) {
            this.id = id;
            this.lowercase = lowercase;
            this.uppercase = uppercase;
            this.special = special;
        }

        public int getId() {
            return id;
        }

        public Character getLowercase() {
            return lowercase;
        }

        public Character getUppercase() {
            return uppercase;
        }

        public Character getSpecial() {
            return special;
        }
    }
}
