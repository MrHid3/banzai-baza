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
	},
	addCategoryToUser: async ({request, cookies, locals}) => {
		const data = await request.formData();

		const res = await serverFetch(
			`/api/member/${data.get("memberUuid")}/category`,
			{
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify({
					id: data.get("categoryId")
				}),
			},
			cookies,
			locals
		)


		if (!res.ok) {
			const body = await res.text();
			return fail(res.status, {
				error: body,
				values: Object.fromEntries(data),
				type: "category"
			});
		}
	},
	deleteCategoryFromUser: async ({request, cookies, locals}) => {
		const data = await request.formData();

		const res = await serverFetch(
			`/api/member/${data.get("memberUuid")}/category/${data.get("categoryId")}`,
			{
				method: 'DELETE'
			},
			cookies,
			locals
		)

		if (!res.ok) {
			const body = await res.text();
			return fail(res.status, {
				error: body,
				values: Object.fromEntries(data),
				type: "category"
			});
		}
	},
};
