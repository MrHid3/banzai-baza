import type { Cookies } from '@sveltejs/kit';
import { redirect } from '@sveltejs/kit';
import { PUBLIC_BACKEND_2 } from '$env/static/public';

export async function serverFetch(
	url: string,
	options: RequestInit,
	cookies: Cookies,
	locals: App.Locals
): Promise<Response> {
	const token = locals.accessToken;

	let res = await fetch(`${PUBLIC_BACKEND_2}${url}`, {
		...options,
		headers: {
			'Content-Type': 'application/json',
			Authorization: `Bearer ${token}`,
			...options.headers
		}
	});

	if (res.status === 403) {
		// Try refresh
		const refreshRes = await fetch(`${PUBLIC_BACKEND_2}/api/auth/refresh`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ refreshToken: cookies.get('refreshToken') })
		});

		if (!refreshRes.ok) redirect(303, '/login');

		const accessToken = await refreshRes.text();
		cookies.set('accessToken', accessToken, {
			path: '/',
			httpOnly: true,
			sameSite: 'strict',
			maxAge: 60 * 15
		});

		// Retry original request with new token
		res = await fetch(`${PUBLIC_BACKEND_2}${url}`, {
			...options,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${accessToken}`,
				...options.headers
			}
		});
	}

	return res;
}
