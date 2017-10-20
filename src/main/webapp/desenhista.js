var svgContainer = d3.select("#svg-container");
var skyImage = svgContainer;

svgContainer.on("click", function(){
    closeColorContainer();
});

function desenhar(imageMetaData) {

	var imgSize = imageMetaData.size || {};

	svgContainer.selectAll("*").remove();

	svgContainer
		.attr("width", imgSize.width)
		.attr("height", imgSize.height);

	for(var i = 0; i < imageMetaData.faces.length; i++) {
		var personFace = imageMetaData.faces[i];

		var faceRect = personFace.face;
		var mouthRect = personFace.mouth;
        var noseRect = personFace.nose;
        var eyesRects = personFace.eyes;

        //Draw face division line
        var faceLineData = personFace.line;
        var faceLine = new PointGenerator(faceLineData[0], faceLineData[1]);
        var faceTopPoint = faceLine.GetFromY(faceRect.y);
        var faceBottomPoint = faceLine.GetFromY(faceRect.y + faceRect.height);



		//Imgproc.line(skyImage, faceTopPoint,faceBottomPoint, new Scalar(0,0,0),1);

		//Draw rect around the face
        //Imgproc.rectangle(skyImage, faceRect, new Scalar(0,0,0));

        //Draw rect around the mouth
        //Imgproc.rectangle(skyImage, mouthRect, new Scalar(0,0,0));

        //Draw rect around the nose
        //Imgproc.rectangle(skyImage, noseRect, new Scalar(0,0,0));

        //Draw eyes rect and circles
        for(var j = 0; j < eyesRects.length; j++) {
        	var eye = eyesRects[j];
            //Imgproc.rectangle(skyImage, eye, new Scalar(0,255,255));
        }

        //Get face feature angle
        var faceFeatureAngle = Math.atan(faceLineData[0]);
        faceFeatureAngle = RadianToDegree(faceFeatureAngle);
        faceFeatureAngle += faceFeatureAngle > 0 ? -90 : 90;

        //Draw circle around face
        var faceCenter = getRectCenter(faceRect);
		var faceSize = new Size(faceRect.width / 2, faceRect.height / 2);

		//Imgproc.ellipse(skyImage, faceCenter, faceSize, 0, 0, 360, new Scalar(172, 203, 227));
		//Imgproc.ellipse(skyImage, faceCenter, faceSize, 0, 0, 360, new Scalar(0,0,0));

    	//Draw face lateral boundaries lines
        //Detect right and left eye
        var rightEye, leftEye;
        if(eyesRects[0].x > eyesRects[1].x)
        {
            rightEye = eyesRects[1];
            leftEye = eyesRects[0];
        }
        else
        {
            rightEye = eyesRects[0];
            leftEye = eyesRects[1];
        }

        //get eye line generator
        var eyeLines = new PointGenerator(getRectCenter(rightEye), getRectCenter(leftEye));

        var leftFacePoint = eyeLines.GetFromX(getRectCenter(leftEye).x + leftEye.width);
        var rightFacePoint = eyeLines.GetFromX(getRectCenter(rightEye).x - rightEye.width);

        //Imgproc.circle(skyImage, leftFacePoint, 10, new Scalar(0,255,0), -1);
        //Imgproc.circle(skyImage, rightFacePoint, 10, new Scalar(255,0,0), -1);



        //Get line generators for each side of the face
        var faceLineSlope = faceLineData[0];

        //Left side
        var leftFaceSideOffset = leftFacePoint.y - leftFacePoint.x * faceLineSlope;
        var leftFaceLine = new PointGenerator(faceLineSlope, leftFaceSideOffset);

        var startPointL = leftFaceLine.GetFromY(0);
        var endPointL = leftFaceLine.GetFromY(imgSize.height);

        //Right side
        var rightFaceSideOffset = rightFacePoint.y - rightFacePoint.x * faceLineSlope;
        var rightFaceLine = new PointGenerator(faceLineSlope, rightFaceSideOffset);

        var startPointR = rightFaceLine.GetFromY(0);
        var endPointR = rightFaceLine.GetFromY(imgSize.height);

        //Imgproc.line(skyImage, startPointL, endPointL, new Scalar(0,255,0));
        //Imgproc.line(skyImage, startPointR, endPointR, new Scalar(255,0,0));

        //Draw mouth line
        //Put center on the top for the mouth stay in the middle of the mouth square
        var mouthCenter = new Point(mouthRect.x + mouthRect.width / 2, mouthRect.y);
        var mouthSize = new Size(mouthRect.width / 2, mouthRect.height / 2);


        var mCenter = getRectCenter(mouthRect);

        //Get mouth line generator
        var aFactMouth = Math.tan(Math.atan(faceLineSlope) + Math.PI / 2);
        var bfactMouth = mCenter.y - mCenter.x * aFactMouth;
        var mouthLine = new PointGenerator(aFactMouth, bfactMouth);

        var leftFaceMouthCrossX = (bfactMouth - leftFaceSideOffset) / (faceLineSlope - aFactMouth);

        var rightFaceMouthCrossX = (bfactMouth - rightFaceSideOffset) / (faceLineSlope - aFactMouth);

        var leftFaceMouthCross = mouthLine.GetFromX(leftFaceMouthCrossX);
        var rightFaceMouthCross = mouthLine.GetFromX(rightFaceMouthCrossX);

        //Get face top line
        var afactTopFace = aFactMouth;   //use the mouth line since this uses the same slope
        var bfactTopFace = faceTopPoint.y - faceTopPoint.x * afactTopFace;
        var faceTopLine = new PointGenerator(afactTopFace, bfactTopFace);

        var leftTopFaceCrossX = (bfactTopFace - leftFaceSideOffset) / (faceLineSlope - afactTopFace);

        var rightTopFaceCrossX = (bfactTopFace - rightFaceSideOffset) / (faceLineSlope - afactTopFace);

        var leftTopFaceCross = faceTopLine.GetFromX(leftTopFaceCrossX);
        var rightTopFaceCross = faceTopLine.GetFromX(rightTopFaceCrossX);

        /*Imgproc.circle(skyImage, leftTopFaceCross, 5, new Scalar(0,128,0), -1);
        Imgproc.circle(skyImage, rightTopFaceCross, 5, new Scalar(0,128,0), -1);
        Imgproc.circle(skyImage, leftFaceMouthCross, 5, new Scalar(0,0,0), -1);
        Imgproc.circle(skyImage, rightFaceMouthCross, 5, new Scalar(0,0,0), -1);
        Imgproc.circle(skyImage, faceBottomPoint, 5, new Scalar(0,0,0), -1);*/

        var facePointsMat = new MatOfPoint(leftTopFaceCross,
            rightTopFaceCross,
            rightFaceMouthCross,
            faceBottomPoint,
            leftFaceMouthCross);

        var skinObj = Imgproc.fillConvexPoly(skyImage, facePointsMat, 0, new Scalar(172, 203, 227));


        skinObj.on("click", function(){

            console.log(d3.event);

            d3.event.cancelBubble = true;

            var eventTarget = d3.select(d3.event.target);

            openColorContainer([
                'rgb(230,174,136)',
                'rgb(229,214,193)',
                'rgb(239,161,93)',
                'rgb(247,194,145)',
                'rgb(133,89,54)'
            ], {
                x:d3.event.layerX,
                y:d3.event.layerY
            }, function(color) {
                eventTarget.style('fill', color);
            });
        });



        var faceWidth = Math.sqrt(Math.pow(rightTopFaceCross.x - leftTopFaceCross.x, 2) + 
            Math.pow(rightTopFaceCross.y - leftTopFaceCross.y, 2));

        var hairCenter = {
            x: rightTopFaceCross.x + (leftTopFaceCross.x - rightTopFaceCross.x) / 2,
            y: rightTopFaceCross.y + (leftTopFaceCross.y - rightTopFaceCross.y) / 2
        }


        //Imgproc.circle(skyImage, hairCenter, 10, new Scalar(0,0,255), -1);

        var hair1 = Imgproc.ellipse(skyImage, hairCenter, {width:faceWidth/2, height: faceWidth/4},faceFeatureAngle, 0, 180, new Scalar(0,0,0), -1);

        var hair2 = Imgproc.ellipse(skyImage, rightTopFaceCross, {width:faceWidth*0.75, height: faceWidth/5},faceFeatureAngle, 270, 360, new Scalar(0,0,0), -1);

        var hair3 = Imgproc.ellipse(skyImage, leftTopFaceCross, {width:faceWidth*0.25, height: faceWidth/5},faceFeatureAngle, 180, 270, new Scalar(0,0,0), -1);

        //Generate a random id for the hair
        var hairId = 'hair-id-' + i;

        hair1.attr('data-hair-id', hairId).on('click', changeHairColor);
        hair2.attr('data-hair-id', hairId).on('click', changeHairColor);
        hair3.attr('data-hair-id', hairId).on('click', changeHairColor);


        //Draw mouth line
        Imgproc.ellipse(skyImage, mouthCenter, mouthSize, faceFeatureAngle, 180, 360, new Scalar(0,0,0), 2);

        var p1 = faceTopLine.GetFromX(0);
        var p2 = faceTopLine.GetFromX(imgSize.width);

        //Draw nose line
        var noseCenter = new Point(noseRect.x + noseRect.width / 2, noseRect.y + noseRect.height / 2);
        var noseSize = new Size(noseRect.width / 2, noseRect.height / 2);
        var noseAngle = Math.atan(faceLineData[0]);
        noseAngle = RadianToDegree(noseAngle);
        Imgproc.ellipse(skyImage, noseCenter, noseSize, noseAngle, 0, 180, new Scalar(0,0,0), 2);

        //Draw eyes ellipses
        for(var j = 0; j < eyesRects.length; j++) {
        	var eye = eyesRects[j];

            var eyeCenter = new Point(eye.x + eye.width / 2, eye.y + eye.height / 2);
            var elipseSize = new Size(eye.width / 5, eye.height / 2);
            Imgproc.ellipse(skyImage, eyeCenter, elipseSize, faceFeatureAngle, 0, 360, new Scalar(0,0,0), -1);
        }

        //Imgproc.line(skyImage, faceBottomPoint, new Point(imgSize.width / 2, imgSize.height), new Scalar(0,0,0));


        //Imgproc.circle(skyImage, faceTopPoint, 5, new Scalar(0,128,0), -1);
        //Imgproc.circle(skyImage, faceBottomPoint, 5, new Scalar(0,128,0), -1);


        var bodyHeight = euclideanDistance(faceTopPoint, faceBottomPoint);
        Imgproc.line(skyImage, faceBottomPoint, 
            new Point(faceBottomPoint.x, faceBottomPoint.y + bodyHeight));

        var membersLength = bodyHeight / 2;

        var bodyArmStart = new Point(faceBottomPoint.x, faceBottomPoint.y + bodyHeight * 0.2);
        var bodyLegStart = new Point(faceBottomPoint.x, faceBottomPoint.y + bodyHeight);         
        
        Imgproc.line(skyImage, bodyArmStart, 
        {
            x: bodyArmStart.x + membersLength * Math.cos(Math.PI/4),
            y: bodyArmStart.y + membersLength * Math.sin(Math.PI/4)
        });

        Imgproc.line(skyImage, bodyArmStart, 
        {
            x: bodyArmStart.x + membersLength * Math.cos(Math.PI - Math.PI/4),
            y: bodyArmStart.y + membersLength * Math.sin(Math.PI - Math.PI/4)
        });



        Imgproc.line(skyImage, bodyLegStart, 
        {
            x: bodyLegStart.x + membersLength * Math.cos(Math.PI/4),
            y: bodyLegStart.y + membersLength * Math.sin(Math.PI/4)
        });

        Imgproc.line(skyImage, bodyLegStart, 
        {
            x: bodyLegStart.x + membersLength * Math.cos(Math.PI - Math.PI/4),
            y: bodyLegStart.y + membersLength * Math.sin(Math.PI - Math.PI/4)
        });

	}


}

//Functions

for (var i = 0; i < 180; i+= 180/4) {
    Imgproc.ellipse(skyImage, {x: 300, y: 300}, {width: 50, height: 100}, i,0,360);    
};

Imgproc.line(skyImage, {x: 300, y: 300}, {x: 500, y: 500});


function changeHairColor(targets) {

    d3.event.cancelBubble = true;

    var hairId = d3.select(this).attr('data-hair-id');

    openColorContainer([
        'rgb(221,221,221)',
        'rgb(232, 220, 124)',
        'rgb(95,93,92)',
        'rgb(252,143,62)',
        'rgb(51,46,35)',
        'rgb(0,0,0)'
    ], {
        x:d3.event.clientX,
        y:d3.event.clientY
    }, function(color) {
        d3.selectAll("[data-hair-id=" + hairId + "]").style({'fill': color, 'stroke': color});
    });
}


function openColorContainer(colorsArray, position, colorCallback) {

    closeColorContainer();

    var colorsContainer = d3.select("body")
        .append("div")
        .classed("colors-container", true)
        .style({
            'top': position.y + 'px',
            'left': position.x + 'px'
        });

    colorsContainer.selectAll('.color-box').data(colorsArray).enter()
        .append('div')
        .classed('color-box', true)
        .style('background-color', function(d) {
            return d;
        })
        .on('click', function(color) {
            d3.event.cancelBubble = true;
            colorCallback(color);
        });
}

function closeColorContainer() {
    d3.selectAll('.colors-container').remove();
}




function getRectCenter(rect) {
	return {
		x: rect.x + rect.width/2,
		y: rect.y + rect.height/2
	}
}

function RadianToDegree(angle) {
    return angle * (180.0 / Math.PI);
}

function DegreeToRadian(angle) {
    return angle * (Math.PI / 180);
}

function euclideanDistance(pt1, pt2) {
    return Math.sqrt(Math.pow(pt1.x - pt2.x, 2) + Math.pow(pt1.y - pt2.y, 2));
}


//Classes
function Scalar(v0, v1, v2) {
	this.rgb = function() {
		return "rgb(" + v2 + "," + v1 + "," + v0 + ")";
	}
}

function Size(width, height) {
	this.width = width;
	this.height = height;
}

function Point(x, y) {
	this.x = x;
	this.y = y;
}

function MatOfPoint() {
	var points = [];

	for (var i = 0; i < arguments.length; i++) {
		points[i] = arguments[i];
	}

	this.getPoints = function() {
		return points;
	}
}

function PointGenerator(aOrP1, bOrP2) {

	var aFact;
    var bFact;

	if(aOrP1.hasOwnProperty("x") && bOrP2.hasOwnProperty("x")) {
		var p1 = aOrP1;
		var p2 = bOrP2;

        if (p1.x == p2.x)
            p1.x += 1;

        aFact = (p1.y - p2.y) / (p1.x - p2.x);
        bFact = p1.y - aFact * p1.x;

	} else {
		var aFact = aOrP1;
    	var bFact = bOrP2;
	}

    this.GetFromX = function(xValue)
    {
    	return {
    		x: xValue,
    		y: aFact * xValue + bFact
    	}
    }

    this.GetFromY = function(yValue)
    {
    	return {
    		x:(yValue - bFact) / aFact,
    		y: yValue
    	}
    }    
}