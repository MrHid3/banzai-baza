import { writable, derived, type Readable } from 'svelte/store';
import { browser } from '$app/environment';
import {goto} from "$app/navigation";

const storedToken = browser ? localStorage.getItem('accessToken') : null;

export const token = writable(storedToken);

token.subscribe((token) => {
	if(!browser)
		return;

	if (token) {
		localStorage.setItem('accessToken', token);
	}else{
		localStorage.removeItem('accessToken');
	}
});

export const isAuthenticated : Readable<boolean> = derived(token, $token => {
	if(!browser)
		return false;

	if (!$token) {
		return false;
	}

	try {
		const payload = JSON.parse(atob($token.split('.')[1]));
		return payload.exp * 1000 > Date.now();
	} catch {
		return false;
	}
})

export function logout() {
	token.set(null);
}