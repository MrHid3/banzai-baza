import type { Handle } from '@sveltejs/kit';
import { redirect } from '@sveltejs/kit';
import { PUBLIC_BACKEND_2, PUBLIC_DEV } from '$env/static/public';

const UNPROTECTED_ROUTES = ['/login', '/api/auth/refresh', '/set-password'];

export const handle: Handle = async ({ event, resolve }) => {
	const accessToken = event.cookies.get('accessToken');
	const refreshToken = event.cookies.get('refreshToken');
	const secure = PUBLIC_DEV !== 'true';

	let activeToken: string | null = null;

	if (accessToken) {
		const user = parseJwt(accessToken);
		if (user && user.exp * 1000 > Date.now()) {
			event.locals.user = { email: user.sub, role: user.role };
			activeToken = accessToken;
		}
	}

	if (!activeToken && refreshToken) {
		const refreshed = await tryRefresh(refreshToken, event.cookies, PUBLIC_BACKEND_2, secure);
		if (refreshed) {
			event.locals.user = refreshed.user;
			activeToken = refreshed.accessToken;
		}
	}

	if (!activeToken) {
		event.locals.user = null;
	}

	if (activeToken) {
		event.locals.accessToken = activeToken;
	}

	const isUnprotected = UNPROTECTED_ROUTES.some((r) => event.url.pathname.startsWith(r));
	if (!isUnprotected && !event.locals.user) {
		redirect(302, '/login');
	}

	return resolve(event);
};

function parseJwt(token: string) {
	try {
		const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
		return JSON.parse(Buffer.from(base64, 'base64').toString('utf-8'));
	} catch {
		return null;
	}
}

async function tryRefresh(refreshToken: string, cookies: any, backendUrl: string, secure: boolean) {
	try {
		const res = await fetch(`${backendUrl}/api/auth/refresh`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ refreshToken })
		});

		if (!res.ok) return null;

		const accessToken = await res.text();
		const user = parseJwt(accessToken);
		if (!user) return null;

		cookies.set('accessToken', accessToken, {
			httpOnly: true,
			secure,
			sameSite: 'strict',
			path: '/',
			maxAge: 60 * 15
		});

		if (refreshToken) {
			cookies.set('refreshToken', refreshToken, {
				httpOnly: true,
				secure,
				sameSite: 'strict',
				path: '/',
				maxAge: 60 * 60 * 24 * 7
			});
		}

		return {
			accessToken: accessToken,
			user: { email: user.sub, role: user.role }
		};
	} catch {
		return null;
	}
}
