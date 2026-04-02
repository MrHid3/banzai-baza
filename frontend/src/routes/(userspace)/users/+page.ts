export const load = async (event) => {

    const appUsers = await event.fetch("/api/appUser");
    const locations = await event.fetch("/api/location/all");

    if (appUsers.ok && locations.ok) {

        return {
            locations: await locations.json(),
            users: await appUsers.json()
        };
    }

}