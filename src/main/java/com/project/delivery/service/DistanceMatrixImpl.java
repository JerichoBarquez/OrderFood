package com.project.delivery.service;

import java.io.IOException;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;

public class DistanceMatrixImpl {

	public DistanceMatrix getDistanceMatrix(LatLng origin, LatLng destination, String apiKey)
			throws ApiException, InterruptedException, IOException {
		GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
		DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
		DistanceMatrix d = req.origins(origin).destinations(destination).await();
		return d;
	}
	

}
