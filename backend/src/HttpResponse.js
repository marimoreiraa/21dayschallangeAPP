class HttpResponse {

    static sendMessage(res, status, message) {
        let payload = { code: status, status: this.#getStatusText(status) }
        if (message) payload.message = message
        this.sendPayload(res, status, payload)
    }

    static sendPayload(res, status, payload) {
        res.status(status).send(payload)
    }

    static #getStatusText(statusCode) {
        switch (statusCode) {
            case 200: return "Ok"
            case 400: return "Bad request"
            case 401: return "Unauthorized"
            case 404: return "Not Found"
            case 409: return "Conflict"
            case 500: return "Internal server error"
             default: return "Undefined status message."
        }
    }
}

module.exports = HttpResponse