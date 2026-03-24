import { isAuthenticated, logout, token } from '../stores/auth.ts';
import {get} from "svelte/store";
import {PUBLIC_BACKEND_2} from "$env/static/public";
import {browser} from "$app/environment";
import {redirect} from "@sveltejs/kit";
import {goto} from "$app/navigation";

export async function refreshAccessToken(customFetch = fetch) {
	if(!browser){
		return null;
	}
    const res = await customFetch(`${PUBLIC_BACKEND_2}/api/auth/refresh`, {
        method: "POST",
        credentials: "include"
    })
    if(!res.ok){
		await logout(customFetch);
	}

	token.set(await res.text())
}

export async function fetchWithAuth(input: RequestInfo, init: RequestInit = {}, customFetch = fetch) : Promise<Response> {
	if (!get(isAuthenticated)) {
		await refreshAccessToken(customFetch);
	}

	let res = await customFetch(`${input}`, {
		...init,
		credentials: 'include',
		headers: {
			...init.headers,
			Authorization: `Bearer ${get(token)}`
		}
	});

	if(!res.ok){
		await refreshAccessToken(customFetch);

		res = await customFetch(`${input}`, {
			...init,
			credentials: 'include',
			headers: {
				...init.headers,
				Authorization: `Bearer ${get(token)}`
			}
		});
	}
	if (!res.ok) {
		await logout(customFetch)
		// if(browser)
		// 	goto("/login")
	}
	return res;
}