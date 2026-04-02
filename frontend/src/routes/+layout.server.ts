import { redirect } from '@sveltejs/kit';

export const load = async ({ locals, url }) => {
	const publicRoutes = ['/login', '/set-password'];

	if (!locals.user && !publicRoutes.includes(url.pathname)) {
		redirect(303, '/login');
	}

	if(locals.user && url.pathname == "/login"){
		redirect(303, '/db');
	}

	return { user: locals.user };
};
