import type { Actions } from './$types';
import { fail } from '@sveltejs/kit';
import { serverFetch } from '$lib/api';

// export const load: PageServerLoad = async ({ cookies }) => {
// 	const res = await serverFetch('/api/locations', { method: 'GET' }, cookies);
// 	const locations = await res.json();
// 	return { locations };
// };

export const actions: Actions = {
	add: async ({ request, cookies, locals }) => {
		const data = await request.formData();

		const res = await serverFetch(
			'/api/member',
			{
				method: 'POST',
				body: JSON.stringify({
					name: data.get('name'),
					surname: data.get('surname'),
					email: data.get('email'),
					phoneNumber: data.get('phoneNumber'),
					monthlyFee: Number(data.get('monthlyFee')),
					locationId: Number(data.get('locationId')),
					comment: data.get('comment')
				})
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? 'Failed to create member',
				values: Object.fromEntries(data)
			});
		}
	},
	delete: async ({ request, cookies, locals }) => {
		const data = await request.formData();

		const res = await serverFetch('/api/member', {
			method: 'DELETE',
			body: JSON.stringify({
				uuid: data.get('uuid')
			}),
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? 'Failed to delete member',
				values: Object.fromEntries(data)
			});
		}
	}
};
