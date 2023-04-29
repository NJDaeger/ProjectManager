export const AUTH_API_V1_BASE: string = "/v1/auth";
export const WORLD_API_V1_BASE: string = "/v1/worlds";
export const PLOT_API_V1_BASE: string = "/v1/plots";
export const TAG_API_V1_BASE: string = "/v1/tags";
export const USER_API_V1_BASE: string = "/v1/users"
export const NO_BACKEND: boolean = true;

export class AUTH_API {
    static LOGIN: string = AUTH_API_V1_BASE + "/login";
    static LOGOUT: string = AUTH_API_V1_BASE + "/logout";
    static VERIFY_AUTHORIZATION: string = AUTH_API_V1_BASE + "/verify_authorized"
}

export class WORLD_API {
    static GET_WORLDS: string = WORLD_API_V1_BASE + "/worldlist";
    static GET_WORLD = (worldId: string): string => WORLD_API_V1_BASE + "?worldId=" + worldId
}

export class PLOT_API {
    static GET_PLOTS = (worldId?: string): string => PLOT_API_V1_BASE + ( worldId ? "?worldId=" + worldId : "");
    static GET_PLOT = (plotId: number): string => PLOT_API_V1_BASE + "?plodId=" + plotId;
}

export class TAG_API {
    static GET_TAGS = (worldId?: string): string => TAG_API_V1_BASE  + ( worldId ? "?worldId=" + worldId : "");;
    static GET_TAG = (tagId: number): string => TAG_API_V1_BASE + "?tagId=" + tagId
}

export class USER_API {
    static GET_SKULL: string = USER_API_V1_BASE + "/skull"
}

export async function safeFetch<T>(input: RequestInfo | URL, init?: RequestInit | undefined, errorMessage?: string): Promise<T> {
    return fetch(input, init).then((res) => {
        if (res.ok) return res.json();
        else throw new Error(errorMessage);
    });
}