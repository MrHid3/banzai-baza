import { type Actions, fail } from '@sveltejs/kit';
import { serverFetch } from '$lib/api.ts';

export const actions : Actions = {
	update: async({request, cookies, locals}) => {
		const data = await request.formData()

		const res = await serverFetch(
			`/api/location/${data.get("id")}`, {
				method: "PUT",
				body: JSON.stringify(
					{
						name: data.get("name"),
						shortname: data.get("shortname")
					}
				)
			},
			cookies,
			locals
		)

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? 'Nie udało się zapisać nazw'
			});
		}

	}
}