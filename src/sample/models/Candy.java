package sample.models;

    public class Candy extends Food {
        public enum Type {Fondant, Jelly, Souffle, Chocolate, Truffles;} // какие типы бывают
        public Type type;// а это собственно тип
        public Candy() {};

        public Candy(int kkal, String title, Type type) {
            super(kkal, title);
            this.type = type;
        }

        @Override
        public String getDescription() {
            String typeString = "";
            switch (this.type)
            {
                case Fondant:
                    typeString = "помадки";
                    break;
                case Jelly:
                    typeString = "желе";
                    break;
                case Souffle:
                    typeString = "суфле";
                    break;
                case Chocolate:
                    typeString = "шоколада";
                    break;
                case Truffles:
                    typeString = "трюфеля";
                    break;
            }

            return String.format("Конфета из %s", typeString);
        }
    }
