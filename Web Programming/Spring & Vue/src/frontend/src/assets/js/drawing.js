import {Y_MAX, Y_MIN, X_MAX, X_MIN} from '@/assets/js/input_validation'

export class Drawer {
    LOCKED_COLOR = "rgba(249, 166, 2, 0.5)"
    UNLOCKED_COLOR = "rgba(119, 102, 92, 0.5)"
    gridWidth = Math.max(Y_MAX - Y_MIN, X_MAX - X_MIN) + 2
    gridHeight = this.gridWidth
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
        return {x: transformed.x * (this.gridWidth / this.canvasW), y: transformed.y * (this.gridHeight / this.canvasH)}
    }

    fromGridToReal({x, y}) {
        const currRealCenter = {x: this.canvasW / 2, y: this.canvasH / 2}
        const unscaled = {x: x / (this.gridWidth / this.canvasW), y: y / (this.gridHeight / this.canvasH)}
        return {x: unscaled.x + currRealCenter.x, y: currRealCenter.y - unscaled.y}
    }

    drawCircle({x, y}, radius = this.canvasW / (this.gridWidth * 2), color =this.UNLOCKED_COLOR) {
        this.ctx.fillStyle = color
        this.ctx.strokeStyle = color
        this.ctx.beginPath();
        this.ctx.arc(x, y, radius, 0, Math.PI * 2, true);
        this.ctx.fill()
        this.ctx.stroke()
    }

    drawArc({x, y}, radius = this.canvasW / (this.gridWidth * 2), color =this.UNLOCKED_COLOR) {
        let previousWidth = this.ctx.lineWidth
        this.ctx.lineWidth = Math.round(radius/6)
        this.ctx.fillStyle = color
        this.ctx.strokeStyle = color
        this.ctx.beginPath();
        this.ctx.arc(x, y, radius, 0, Math.PI * 2, true);
        const startPos = this.fromGridToReal({x: 0, y: 0})
        this.ctx.moveTo(x, y)
        this.ctx.arc(x, y, radius/5, 0, Math.PI * 2, true);
        this.ctx.stroke()
        this.ctx.lineWidth = previousWidth
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
        let left_down = this.fromGridToReal({x: X_MIN, y: Y_MIN})
        let left_up = this.fromGridToReal({x: X_MIN, y: Y_MAX})
        let right_down = this.fromGridToReal({
            x: X_MAX,
            y: Y_MIN
        })
        let right_up = this.fromGridToReal({
            x: X_MAX,
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
        const startPos = this.fromGridToReal({x: 0, y: 0})
        this.ctx.moveTo(startPos.x, startPos.y)
        let pointsToArc = [
            [-R, 0],
            [-R, -R / 2],
            [0, -R / 2],
            [R, 0]]
        for (const item in pointsToArc) {
            let realCoords = this.fromGridToReal({
                x: pointsToArc[item][0],
                y: pointsToArc[item][1]
            })
            this.ctx.lineTo(realCoords.x, realCoords.y)
        }
        let arcCoords = this.fromGridToReal({x: 0, y: 0})
        this.ctx.arc(arcCoords.x, arcCoords.y, R * Math.max(this.canvasW / this.gridWidth, this.canvasH / this.gridWidth), 0, -Math.PI / 2, true)
        pointsToArc = [
            [R / 2, 0],
            [0, R / 2]]
        const arcEndPosition = this.fromGridToReal({x: R / 2, y: 0})
        this.ctx.moveTo(arcEndPosition.x, arcEndPosition.y)
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

    redrawScene(shotsArr, coords, isInChildMode = false, zoneRadius, targetRadius = this.canvasW / (this.gridWidth * 2), targetColor) {
        this.ctx.clearRect(0, 0, this.canvasW, this.canvasH);
        this.drawGraph();
        this.drawAllowedBorder();
        if (isInChildMode) {
            this.drawZones(zoneRadius)
        }
        shotsArr.forEach((item) => {
            let hitColor = item.hit ? "rgba(119, 255, 92, 0.5)" : "rgba(255, 102, 92, 0.5)"
            this.drawCircle(this.fromGridToReal({
                x: item.x,
                y: item.y
            }), this.canvasW / (this.gridWidth * 2), hitColor)
        })

        this.drawArc(this.fromGridToReal(coords), targetRadius, targetColor)
    }

}



