import { PUBLIC_BACKEND_2, PUBLIC_DEV } from '$env/static/public';
import { fail, redirect } from '@sveltejs/kit';
import { user } from '$lib/stores/auth';

export const load = ({ locals }) => {
	if (locals.user) {
		redirect(301, '/db');
	}
};

export const actions = {
	login: async ({ request, cookies, fetch }) => {
		const data = await request.formData();
		const email = data.get('email');
		const password = data.get('password');

		const res = await fetch(`${PUBLIC_BACKEND_2}/api/auth/login`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ email, password })
		});

		if (!res.ok) return fail(400, { error: await res.text() });

		const { accessToken, refreshToken } = await res.json();
		const isProd = !PUBLIC_DEV;

		cookies.set('accessToken', accessToken, {
			httpOnly: true,
			secure: isProd,
			sameSite: 'strict',
			path: '/',
			maxAge: 60 * 15 // 15 minutes
		});

		cookies.set('refreshToken', refreshToken, {
			httpOnly: true,
			secure: isProd,
			sameSite: 'strict',
			path: '/',
			maxAge: 60 * 60 * 24 * 30 // 30 days
		});

		redirect(303, '/db');
	}
};
