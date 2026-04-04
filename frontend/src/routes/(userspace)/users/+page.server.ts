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
    },

    addLocationToUser: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            '/api/appUser/location',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userUuid: data.get("userUuid"),
                    locationId: data.get("locationId")
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
    },

    deleteLocationFromUser: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            '/api/appUser/location',
            {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userUuid: data.get("userUuid"),
                    locationId: data.get("locationId")
                }),
            },
            cookies,
            locals
        )

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Nie udało się usunąć lokalizacji',
                values: Object.fromEntries(data)
            });
        }
    },

    deleteLocation: async ({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            `/api/location?locationId=${data.get("locationId")}`,
            {
                method: 'DELETE'
            },
            cookies,
            locals
        )

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Nie udało się usunąć lokalizacji',
                values: Object.fromEntries(data)
            });
        }
    },

    changeStatus: async({request, cookies, locals}) => {
        const data = await request.formData();

        const res = await serverFetch(
            `/api/appUser/status`,
            {
                method: 'POST',
                body: JSON.stringify({
                    userUuid: data.get("userUuid"),
                    status: data.get("status")
                })
            },
            cookies,
            locals
        )

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            return fail(res.status, {
                error: body.message ?? 'Nie udało się zmienić statusu',
                values: Object.fromEntries(data)
            });
        }
    }
}