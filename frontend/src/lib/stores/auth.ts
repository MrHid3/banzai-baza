import { browser } from '$app/environment';
import { goto } from '$app/navigation';

export async function logout(customFetch = fetch) {
	if (browser) {
		await customFetch('/logout', { method: 'POST' });
		await goto('/login');
	}
}
