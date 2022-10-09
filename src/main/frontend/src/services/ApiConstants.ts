export const AUTH_API_V1_BASE: string = "/v1/auth";

export class AUTH_API {
    static LOGIN: string = AUTH_API_V1_BASE + "/login";
    static LOGOUT: string = AUTH_API_V1_BASE + "/logout";
    static VERIFY_AUTHORIZATION: string = AUTH_API_V1_BASE + "/verify_authorized"
}

export async function safeFetch(input: RequestInfo | URL, init?: RequestInit | undefined, errorMessage?: string): Promise<Response> {
    return fetch(input, init).then((res) => {
        if (res.ok) return res.json();
        else throw new Error(errorMessage);
    });
}