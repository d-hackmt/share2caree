/**
 * Location Picker Module
 * Handles Google Maps initialization, Geolocation, and form syncing.
 */

let map;
let marker;
let autocomplete;
let selectedLat;
let selectedLng;

function initLocationPicker(mapId, searchInputId, latInputId, lngInputId) {
    const defaultLocation = { lat: 19.0760, lng: 72.8777 }; // Default to Mumbai
    
    // Initialize Map
    map = new google.maps.Map(document.getElementById(mapId), {
        center: defaultLocation,
        zoom: 13,
        mapTypeControl: false,
        fullscreenControl: false,
        streetViewControl: false,
        styles: [
            {
                "featureType": "all",
                "elementType": "labels.text.fill",
                "stylers": [{"saturation": 36}, {"color": "#333333"}, {"lightness": 40}]
            },
            {
                "featureType": "landscape",
                "elementType": "all",
                "stylers": [{"color": "#f2f2f2"}]
            }
        ]
    });

    // Initialize Marker
    marker = new google.maps.Marker({
        map: map,
        draggable: true,
        animation: google.maps.Animation.DROP,
        position: defaultLocation
    });

    // Sync marker drag with inputs
    marker.addListener('dragend', function() {
        const pos = marker.getPosition();
        updateLocationInputs(pos.lat(), pos.lng(), latInputId, lngInputId);
    });

    // Sync map click with marker and inputs
    map.addListener('click', function(e) {
        const pos = e.latLng;
        marker.setPosition(pos);
        updateLocationInputs(pos.lat(), pos.lng(), latInputId, lngInputId);
    });

    // Initialize Autocomplete
    const searchInput = document.getElementById(searchInputId);
    autocomplete = new google.maps.places.Autocomplete(searchInput);
    autocomplete.bindTo('bounds', map);

    autocomplete.addListener('place_changed', function() {
        const place = autocomplete.getPlace();
        if (!place.geometry) return;

        if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
        } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17);
        }
        marker.setPosition(place.geometry.location);
        updateLocationInputs(place.geometry.location.lat(), place.geometry.location.lng(), latInputId, lngInputId);
    });
}

function updateLocationInputs(lat, lng, latId, lngId) {
    selectedLat = lat;
    selectedLng = lng;
    document.getElementById(latId).value = lat;
    document.getElementById(lngId).value = lng;
}

function getCurrentLocation(latId, lngId) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const pos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude,
                };
                map.setCenter(pos);
                map.setZoom(17);
                marker.setPosition(pos);
                updateLocationInputs(pos.lat, pos.lng, latId, lngId);
            },
            () => {
                alert("Error: The Geolocation service failed.");
            }
        );
    } else {
        alert("Error: Your browser doesn't support geolocation.");
    }
}

// Make functions globally available
window.initLocationPicker = initLocationPicker;
window.getCurrentLocation = getCurrentLocation;
