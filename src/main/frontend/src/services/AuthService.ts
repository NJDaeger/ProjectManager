import { LoginRequestModel, LogoutRequestModel, AuthorizeRequestModel } from "../models/AuthModels";
import { User } from "../models/UserModels";
import { AUTH_API, safeFetch } from "./ApiConstants";

/**
 * Perform a login
 * @param login The login parameters
 * @returns The logged in user
 */
export async function login(login: LoginRequestModel): Promise<User> {
    return safeFetch(AUTH_API.LOGIN, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(login)
    });
}

/**
 * Perform a logout
 * @param logout The logout parameters
 * @returns The fetch response promise
 */
export async function logout(logout: LogoutRequestModel): Promise<Response>  {
    return safeFetch(AUTH_API.LOGOUT, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(logout)
    });
}

/**
 * Verify the current user is authorized for this action
 * @param route The operation to verify
 * @returns The fetch response promise
 */
export async function verifyAuthorized(route: AuthorizeRequestModel): Promise<Response> {
    return safeFetch(AUTH_API.VERIFY_AUTHORIZATION, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(route)
    }, "Unauthorized");
}