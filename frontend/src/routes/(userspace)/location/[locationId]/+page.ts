export const load: PageLoad = async (event) => {
    const locationRequest = await event.fetch(`/api/location/${event.params.locationId}`, {method: "GET"});

    if (locationRequest) {
        const location = await locationRequest.json();
        return { location: location};
    } else {
        return { error: true };
    }
};
