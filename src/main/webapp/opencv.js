var Imgproc = {

	line: function(image, point1, point2, color, thickness) {
		color = color || new Scalar(0,0,0);
		thickness = thickness || 1;

		image.append("line")
			.attr("x1", point1.x)
			.attr("y1", point1.y)
			.attr("x2", point2.x)
			.attr("y2", point2.y)
			.style({
				'stroke': color.rgb(),
				'stroke-width': thickness + 'px'
			});
	},

	rectangle: function(image, rect, color, thickness) {
		color = color || new Scalar(0,0,0);
		thickness = thickness == undefined ? 1 : thickness;

		image.append("rect")
			.attr("x", rect.x)
			.attr("y", rect.y)
			.attr("width", rect.width)
			.attr("height", rect.height)
			.style({
				'stroke': color.rgb(),
				'stroke-width': thickness + 'px',
				'fill': thickness < 0 ? color.rgb() : 'transparent'
			});
	}, 

	circle: function(image, center, radius, color, thickness) {
		color = color || new Scalar(0,0,0);
		thickness = thickness == undefined ? 1 : thickness;

		image.append("circle")
			.attr("cx", center.x)
			.attr("cy", center.y)
			.attr("r", radius)
			.style({
				'stroke': color.rgb(),
				'stroke-width': thickness + 'px',
				'fill': thickness < 0 ? color.rgb() : 'transparent'
			});

	},

	ellipse: function(image, center, size, angle, startAngle, endAngle, color, thickness) {
		color = color || new Scalar(0,0,0);
		thickness = thickness == undefined ? 1 : thickness;

		if(startAngle == 360)
			startAngle = 359.99;

		if(endAngle == 360)
			endAngle = 359.99;

		var largeArcFlag = endAngle - startAngle < 180 ? "0" : "1";

		//Convert to radian
		startAngle *= Math.PI / 180;
		endAngle *= Math.PI / 180;

		var startX = size.width * Math.cos(startAngle);
		var startY = size.height * -Math.sin(startAngle);

		var endX = size.width * Math.cos(endAngle);
		var endY = size.height * -Math.sin(endAngle);

		var ellipsePath = "M" + startX + "," + startY + " A" + size.width + "," + size.height + " 0 " + largeArcFlag + " 0 " + endX + "," + endY;

		if(thickness < 0)
			ellipsePath += " L0,0z";

		return image.append("path")
			.attr("d", ellipsePath)
			.attr("transform", "translate(" + center.x + " " + center.y + ")rotate(" + angle + ")")
			.style({
				'stroke': color.rgb(),
				'stroke-width': thickness + 'px',
				'fill': thickness < 0 ? color.rgb() : 'transparent'
			});
	},

	fillConvexPoly: function(image, points, numberOfPoints, color) {
		color = color || new Scalar(0,0,0);

		return image.append("path")
			.attr("d", getPolygonPath(points.getPoints(), true))
			.style({
				'fill': color.rgb()
			});
	}

}

function getPolygonPath(points, closed) {

	var path = "";

	for (var i = 0; i < points.length; i++) {
		var point = points[i];

		if(i == 0) {
			path += "M" + point.x + "," + point.y + " ";
		} else {
			path += "L" + point.x + "," + point.y + " ";
		}

		if(closed && (i + 1) == points.length)
			path += "z";
	}

	return path;
}