import { redirect } from '@sveltejs/kit';

export const load = async ({ locals, url }) => {
	const publicRoutes = ['/login'];

	if (!locals.user && !publicRoutes.includes(url.pathname)) {
		redirect(303, '/login');
	}

	if(locals.user && url.pathname == "/login"){
		redirect(303, '/baza');
	}

	return { user: locals.user };
};
