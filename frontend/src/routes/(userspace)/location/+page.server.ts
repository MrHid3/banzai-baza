import { serverFetch } from '$lib/api.ts';
import { type Actions, fail } from '@sveltejs/kit';

export const actions: Actions = {
	addLocation: async ({ request, cookies, locals }) => {
		const data = await request.formData();

		const res = await serverFetch(
			'/api/location',
			{
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					name: data.get('name'),
					shortname: data.get('shortname')
				})
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.text();
			return fail(res.status, {
				error: body,
				values: Object.fromEntries(data),
				type: 'location'
			});
		}

		return {
			ok: true
		}
	}
};
