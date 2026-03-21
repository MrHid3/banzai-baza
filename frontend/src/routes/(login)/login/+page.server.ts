import { PUBLIC_BACKEND_2, PUBLIC_DEV } from '$env/static/public';
import { fail, redirect } from '@sveltejs/kit';

export const actions = {
	login: async ({ request, cookies }) => {
		const data = await request.formData();
		const email = data.get('email');
		const password = data.get('password');

		const res = await fetch(`${PUBLIC_BACKEND_2}/api/auth/login`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({
				email: email,
				password: password
			})
		});

		const { accessToken, refreshToken } = await res.json();

		cookies.set('refreshToken', refreshToken, {
			httpOnly: true,
			secure: !PUBLIC_DEV,
			sameSite: 'strict',
			path: `/api/auth/refresh`
		});
		if (!res.ok) return fail(400, { error: await res.text() });
		// throw redirect(303, '/db')
		return { success: true, token: await accessToken };
	}
};
