package com.project.delivery.service;

import java.io.IOException;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.project.delivery.exception.DeliveryGlobalException;

public class DistanceMatrixImpl {

	public static DistanceMatrix getDistanceMatrix(String[] origin, String[] destination)
			throws ApiException, InterruptedException, IOException {

		GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyB1kvAJG6g3PHdlgjk7fl8tLl1YZL1p7Ww").build();
		DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
		DistanceMatrix d = req.origins(latlang(origin)).destinations(latlang(destination)).await();
		return d;

	}
	public static LatLng latlang(String[] coordinates) {
		try {
			if (coordinates.length == 2) {
				return new LatLng(new Double(coordinates[0]), new Double(coordinates[1]));
			}
		} catch (NumberFormatException e) {
			throw new DeliveryGlobalException("Invalid input");
		}
		return null;

	}

}
