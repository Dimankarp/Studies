import {X_ALLOWED_ARR, Y_MAX, Y_MIN} from './input_validator.js'


export const LOCKED_COLOR = "rgba(249, 166, 2, 0.5)"
export const UNLOCKED_COLOR = "rgba(119, 102, 92, 0.5)"
const gridWidth = X_ALLOWED_ARR.length + 1
const gridHeight = Y_MAX - Y_MIN + 2

export class Drawer {
    canvasW;
    canvasH;
    ctx;

    constructor(context, canvasW, canvasH) {
        this.canvasW = canvasW
        this.canvasH = canvasH
        this.ctx = context
    }


    fromRealToGrid({x, y}) {
        const currRealCenter = {x: this.canvasW / 2, y: this.canvasH / 2}
        const transformed = {x: x - currRealCenter.x, y: currRealCenter.y - y}
        return {x: transformed.x * (gridWidth / this.canvasW), y: transformed.y * (gridHeight / this.canvasH)}
    }

    fromGridToReal({x, y}) {
        const currRealCenter = {x: this.canvasW / 2, y: this.canvasH / 2}
        const unscaled = {x: x / (gridWidth / this.canvasW), y: y / (gridHeight / this.canvasH)}
        return {x: unscaled.x + currRealCenter.x, y: currRealCenter.y - unscaled.y}
    }

    drawCircle({x, y}, radius = this.canvasW / (gridWidth * 2), color = UNLOCKED_COLOR) {
        this.ctx.fillStyle = color
        this.ctx.strokeStyle = color
        this.ctx.beginPath();
        this.ctx.arc(x, y, radius, 0, Math.PI * 2, true);
        this.ctx.fill()
        this.ctx.stroke()
    }


    drawGraph(color = "rgba(119, 102, 92, 0.5)") {
        const currRealCenter = {x: this.canvasW / 2, y: this.canvasH / 2}
        this.ctx.fillStyle = color
        this.ctx.strokeStyle = color
        this.ctx.beginPath()
        this.ctx.moveTo(currRealCenter.x, 0)
        this.ctx.lineTo(currRealCenter.x, this.canvasH)
        this.ctx.moveTo(0, currRealCenter.y)
        this.ctx.lineTo(this.canvasW, currRealCenter.y)
        this.ctx.stroke()
    }

    drawAllowedBorder(color = "rgba(119, 102, 92, 0.5)") {
        this.ctx.fillStyle = color
        this.ctx.strokeStyle = color
        let left_down = this.fromGridToReal({x: X_ALLOWED_ARR[0], y: Y_MIN})
        let left_up = this.fromGridToReal({x: X_ALLOWED_ARR[0], y: Y_MAX})
        let right_down = this.fromGridToReal({
            x: X_ALLOWED_ARR[X_ALLOWED_ARR.length - 1],
            y: Y_MIN
        })
        let right_up = this.fromGridToReal({
            x: X_ALLOWED_ARR[X_ALLOWED_ARR.length - 1],
            y: Y_MAX
        })
        this.ctx.beginPath()
        this.ctx.moveTo(left_up.x, left_up.y)
        this.ctx.lineTo(left_down.x, left_down.y)
        this.ctx.lineTo(right_down.x, right_down.y)
        this.ctx.lineTo(right_up.x, right_up.y)
        this.ctx.lineTo(left_up.x, left_up.y)
        this.ctx.closePath()
        this.ctx.stroke()
    }

    drawZones(radius, color = "rgba(119, 102, 92, 0.5)") {
        this.ctx.fillStyle = color
        this.ctx.strokeStyle = color
        const R = radius
        this.ctx.beginPath()
        const startPos = this.fromGridToReal({x: -R, y: -R / 2})
        this.ctx.moveTo(startPos.x, startPos.y)
        let pointsToArc = [
            [0, -R / 2],
            [0, 0],
            [R, 0]]
        for (const item in pointsToArc) {
            let realCoords = this.fromGridToReal({
                x: pointsToArc[item][0],
                y: pointsToArc[item][1]
            })
            this.ctx.lineTo(realCoords.x, realCoords.y)
        }
        let arcCoords = this.fromGridToReal({x: 0, y: 0})
        this.ctx.arc(arcCoords.x, arcCoords.y, R * (this.canvasW / gridWidth), 0, 3 * Math.PI / 2, true)
        pointsToArc = [
            [0, R / 2],
            [-R, 0]]
        for (const item in pointsToArc) {
            let realCoords = this.fromGridToReal({
                x: pointsToArc[item][0],
                y: pointsToArc[item][1]
            })
            this.ctx.lineTo(realCoords.x, realCoords.y)
        }
        this.ctx.lineTo(startPos.x, startPos.y)
        this.ctx.closePath()
        this.ctx.fill()


    }

    redrawScene(shotsArr, coords, isInChildMode = false, zoneRadius, targetRadius=this.canvasW / (gridWidth * 2), targetColor) {
        this.ctx.clearRect(0, 0, this.canvasW,  this.canvasH);
        this.drawGraph();
        this.drawAllowedBorder();
        if (isInChildMode) {
            this.drawZones(zoneRadius)
        }
        shotsArr.forEach((item) => {
            let hitColor = item.isHit ? "rgba(119, 255, 92, 0.5)" : "rgba(255, 102, 92, 0.5)"
            this.drawCircle(this.fromGridToReal({x: item.xCoord, y: item.yCoord}), this.canvasW / (gridWidth * 2), hitColor)
        })

        this.drawCircle(this.fromGridToReal(coords), targetRadius, targetColor)
    }

}



