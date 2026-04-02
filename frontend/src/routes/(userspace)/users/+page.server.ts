import {serverFetch} from "$lib/api";
import {type Actions, fail} from "@sveltejs/kit";


export const actions : Actions = {
    invite: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            '/api/auth/registration/invite',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: data.get("email"),
                    role: data.get("role")
                }),
            },
            cookies,
            locals
        )

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Failed to invite user',
                values: Object.fromEntries(data)
            });
        }
    },

    addLocation: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            '/api/location',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name: data.get("name"),
                    shortname: data.get("shortname")
                }),
            },
            cookies,
            locals
        )

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Failed to add location',
                values: Object.fromEntries(data)
            });
        }
    }
}