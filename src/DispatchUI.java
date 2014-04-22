import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;


public class DispatchUI extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DispatchUI(Composite parent, int style) {
		super(parent, style);
		setLayout(null);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(0, 21, 450, 279);
		text.setCursor(getCursor());
		text.append("ddd");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
