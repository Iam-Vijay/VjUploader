package `in`.angryvijay.vjuploader.utils
data class Controller<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Controller<T> {
            return Controller(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? ): Controller<T> {
            return Controller(Status.ERROR, data, message)
        }

        fun <T> loading(data: T?): Controller<T> {
            return Controller(Status.LOADING, data, null)
        }
    }
}