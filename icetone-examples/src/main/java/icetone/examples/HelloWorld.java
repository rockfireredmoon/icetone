package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Panel;
import icetone.controls.table.Table;
import icetone.controls.table.TableRow;
import icetone.core.Measurement.Unit;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.FillLayout;

public class HelloWorld extends SimpleApplication {

	public static void main(String[] args) {
		new HelloWorld().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).showElement(new Panel(new FillLayout()) {
			{
				addElement(new Table() {
					{
						for (int i = 0; i < 5; i++)
							addColumn("Col " + i);
						for (int i = 0; i < 10; i++) {
							String r = "Row " + i;
							addRow(new TableRow(this) {
								{
									for (int j = 0; j < 5; j++)
										addCell(r + ", Col " + j, j);
								}
							});
						}
					}
				});
				setPreferredDimensions(new Size(480, 0, Unit.PX, Unit.AUTO));
				setPosition(100, 100);
			}
		});
	}

}