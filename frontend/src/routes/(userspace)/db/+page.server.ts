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
				error: body.message ?? 'Nie udało się stworzyć członka',
				values: Object.fromEntries(data)
			});
		}
	},
	delete: async ({ request, cookies, locals }) => {
		const data = await request.formData();

		const res = await serverFetch('/api/member', {
			method: 'DELETE',
			body: JSON.stringify({
				uuid: data.get('memberUuid')
			}),
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? 'Nie udało się usunąć członka',
				values: Object.fromEntries(data)
			});
		}

		return {
			type: "delete",
			uuid: data.get("memberUuid"),
			ok: true
		}
	},

	undelete: async ({ request, cookies, locals }) => {
		const data = await request.formData();

		const res = await serverFetch('/api/member/undelete', {
				method: 'POST',
				body: JSON.stringify({
					uuid: data.get('memberUuid')
				}),
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? "Nie udało się cofnąć usunięcia członka",
				values: Object.fromEntries(data)
			});
		}

		return {
			type: "undelete",
			uuid: data.get("memberUuid"),
			ok: true
		}
	},
	update: async ({ request, cookies, locals }) => {
		const data = await request.formData();
		const uuid = data.get('memberUuid');

		const res = await serverFetch(
			`/api/member/${uuid}`,
			{
				method: 'PUT',
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
				error: body.message ?? 'Nie udało się zaktualizować członka',
				values: Object.fromEntries(data)
			});
		}
	}
};
