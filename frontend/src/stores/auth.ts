import { writable, derived, type Readable } from 'svelte/store';
import { browser } from '$app/environment';
import {goto} from "$app/navigation";
import {redirect} from "@sveltejs/kit";

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

export async function logout(customFetch = fetch) {
	if(browser){
		console.log("Aaaa")
		token.set(null);
		await customFetch('/logout', { method: 'POST' });
		await goto('/login')
	}
	// throw redirect(303, '/login');
	// goto('/login')
}