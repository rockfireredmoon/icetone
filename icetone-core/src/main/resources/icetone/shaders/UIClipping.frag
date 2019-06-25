void main() {
	if (pos.x < clipping.x || pos.x > clipping.z || 
		pos.y < clipping.y || pos.y > clipping.w) {
		discard;
		return;
	}
	
    outColor = color;
}
