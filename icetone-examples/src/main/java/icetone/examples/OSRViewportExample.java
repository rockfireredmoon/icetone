package icetone.examples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

import icetone.controls.containers.Frame;
import icetone.controls.containers.OSRViewPort;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Measurement.Unit;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ColorField;

/**
 * This example shows some examples of usage of the {@link OSRViewPort}, that
 * allows 3D objects to be added to the 2D scene.
 */
public class OSRViewportExample extends SimpleApplication {

	public static void main(String[] args) {
		OSRViewportExample app = new OSRViewportExample();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		/*
		 * We are only using a single screen, so just initialise it (and you don't need
		 * to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help ExampleRunner so
		 * this example can be run from there and as a standalone JME application
		 */
		buildExample(new Screen(this));

	}

	protected void buildExample(ElementContainer<?, ?> container) {
		container.showElement(new Frame() {
			{
				setTitle("Model Table");
				setResizable(true);
				getContentArea().setPreferredDimensions(new Size(600, 0, Unit.PX, Unit.AUTO));
				getContentArea().setLayoutManager(new MigLayout(screen, "", "[fill, grow]", "[fill, grow]"));
				getContentArea().addElement(new Table() {

					List<TableRow> lastSelected;

					{
						onChanged((e) -> {
							if (lastSelected != null) {
								for (TableRow r : lastSelected) {
									final Iterator<BaseElement> iterator = r.getElements().iterator();
									iterator.next();
									OSRViewPort vp = (OSRViewPort) iterator.next().getElements().iterator().next();
									vp.getOSRBridge().getRootNode().getChild(0).removeControl(Rotator.class);
								}
							}
							final List<TableRow> selectedRows = getSelectedRows();
							for (TableRow r : selectedRows) {
								final Iterator<BaseElement> iterator = r.getElements().iterator();
								iterator.next();
								OSRViewPort vp = (OSRViewPort) iterator.next().getElements().iterator().next();
								vp.getOSRBridge().getRootNode().getChild(0).addControl(new Rotator());
							}
							lastSelected = new ArrayList<TableRow>(selectedRows);
						});

						addColumn("Name");
						addColumn("Model");
						addColumn("Size");
						addColumn("Colour");

						addRow(createRow(1, new Geometry("Box", new Box(1, 1, 1)), "Box"));
						addRow(createRow(1, new Geometry("Cyclinder", new Cylinder(8,8,1f,1f)), "Cylinder"));
						addRow(createRow(1, new Geometry("Sphere", new Sphere(8,8,1f)), "Sphere"));

						setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);
					}

					TableRow createRow(int row, Geometry geom, String name) {
						TableRow row1 = new TableRow(screen, this);

						TableCell cell1 = new TableCell(screen, name, "Name " + row);
						row1.addElement(cell1);

						TableCell cell2 = new TableCell(screen, null, "Model " + row);

						Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
						mat.setColor("Color", ColorRGBA.Blue);
						mat.getAdditionalRenderState().setWireframe(true);
						geom.setMaterial(mat);

						//
						Node n = new Node();
						AmbientLight al = new AmbientLight();
						al.setColor(ColorRGBA.White);
						n.addLight(al);
						n.attachChild(geom);

						cell2.addElement(new OSRViewPort(screen, 100, 100, n).setIgnoreMouse(true));

						row1.addElement(cell2);
						TableCell cell3 = new TableCell(screen, null, "Density " + row);
						Spinner<Float> sp1 = new Spinner<Float>(screen, Orientation.HORIZONTAL, true);
						cell3.setVAlign(BitmapFont.VAlign.Center);
						sp1.setSpinnerModel(new FloatRangeSpinnerModel(0, 10, 1, row));
						cell3.addElement(sp1);
						row1.addElement(cell3);

						TableCell cell4 = new TableCell(screen, null, "Colour " + row);
						cell4.setHAlign(BitmapFont.Align.Right);
						ColorField cfc = new ColorField(screen, ColorRGBA.White);
						cell4.addElement(cfc);
						row1.addElement(cell4);

						return row1;
					}
				});
			}
		});

	}

	class Rotator extends AbstractControl {

		private float speed;

		public Rotator() {
			this(1);

		}

		public Rotator(float speed) {
			this.speed = speed;
		}

		public float getSpeed() {
			return speed;
		}

		public void setSpeed(float speed) {
			this.speed = speed;
		}

		@Override
		protected void controlUpdate(float tpf) {
			spatial.rotate(0, tpf * speed, 0);
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}
	}

}
