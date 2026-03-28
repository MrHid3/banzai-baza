import { writable, derived, type Readable } from 'svelte/store';
import { browser } from '$app/environment';
import { goto } from '$app/navigation';

// Holds the user object passed from the server (no token)
export const user = writable<{ email: string; role: string } | null>(null);

export const isAuthenticated: Readable<boolean> = derived(user, ($user) => $user !== null);

export async function logout(customFetch = fetch) {
	if (browser) {
		user.set(null);
		await customFetch('/logout', { method: 'POST' });
		await goto('/login');
	}
}
