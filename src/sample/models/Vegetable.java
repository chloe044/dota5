package sample.models;

    public class Vegetable extends Food {
        public Boolean isFresh;// свежий ли
        public Vegetable() {};

        public Vegetable(int kkal, String title, Boolean isFresh) {
            super(kkal, title);
            this.isFresh = isFresh;
        }

        @Override
        public String getDescription() {
            String isFreshString = this.isFresh ? "свежий" : "не свежий";
            return String.format("Овощ %s", isFreshString);
        }
    }
