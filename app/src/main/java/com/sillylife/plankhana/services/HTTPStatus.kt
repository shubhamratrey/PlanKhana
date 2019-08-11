package com.sillylife.plankhana.servicesenum class HTTPStatus constructor(val code: Int, val message: String) {    OK(200, "Ok"),    CREATED(201, "Created"),    NO_CONTENT(204, "No Content"),    BAD_REQUEST(400,"Bad Request"),    UNAUTHORIZED(401, "Unauthorized"),    FORBIDDEN(403, "Forbidden"),    NOT_FOUND(404,"Not Found"),    SERVER_ERROR(500, "Internal Server Error"),    SOCKET_TIMEOUT(504, "Socket Timeout Exception");    companion object {        fun getHTTPStatus(code: Int): HTTPStatus {            for (httpstatus in HTTPStatus.values()) {                if (code == httpstatus.code) {                    return httpstatus                }            }            return OK        }    }}