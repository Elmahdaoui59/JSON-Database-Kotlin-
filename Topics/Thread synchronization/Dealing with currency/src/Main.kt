@SuppressWarnings("MagicNumber")
class BankAccount {
    var balance = 0L

    fun addMoney(action: String) {
        // synchronize adding money with synchronized()
        synchronized(this) {
            when (action) {
                "1" -> {
                    addGold()
                }

                "2" -> {
                    addSilver()
                }

                else -> {
                    addCopper()
                }
            }
        }
    }

    fun addGold() {
        balance += 10000
    }

    fun addSilver() {
        balance += 100
    }

    fun addCopper() {
        balance += 1
    }
}