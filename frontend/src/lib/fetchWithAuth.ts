import {isAuthenticated, logout, token} from '../stores/auth.ts';
import {get} from "svelte/store";

export async function refreshAccessToken(customFetch = fetch) : Promise<string | null> {
    const res = await customFetch(`/api/auth/refresh`, {
        method: "POST",
        credentials: "include"
    })

    if(!res.ok) return null;

    return await res.text();
}

export async function fetchWithAuth(input: RequestInfo, init: RequestInit = {}, customFetch = fetch) : Promise<Response> {
	if (!get(isAuthenticated)) {
		const newToken = await refreshAccessToken(customFetch);

		if (newToken) {
			token.set(newToken);
		} else {
			logout();
			// await goto('/login');
		}
	}

	return await customFetch(`${input}`, {
		...init,
		credentials: 'include',
		headers: {
			...init.headers,
			Authorization: `Bearer ${get(token)}`
		}
	});
}