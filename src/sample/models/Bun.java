package sample.models;

import java.util.ArrayList;

public class Bun extends Food // булочка
    {
        public Boolean withRaisins;// пшеничный
        public Boolean withMilk;// ржаной
        public Boolean withCinnamon;// зерновой
        public Boolean withPoppy;// отрубной
        public Bun() {};

        public Bun(int kkal, String title, Boolean withRaisins, Boolean withMilk, Boolean withCinnamon, Boolean withPoppy) {
            super(kkal, title);
            this.withRaisins = withRaisins;
            this.withMilk = withMilk;
            this.withCinnamon = withCinnamon;
            this.withPoppy = withPoppy;
        }

        @Override
        public String getDescription() {
            ArrayList<String> items = new ArrayList<>();
            if (this.withRaisins)
                items.add("с изюмом");
            if (this.withMilk)
                items.add("со сгущенкой");
            if (this.withCinnamon)
                items.add("с корицей");
            if (this.withPoppy)
                items.add("с маком");

            return String.format("Булочка %s", String.join(", ", items));
        }

    }