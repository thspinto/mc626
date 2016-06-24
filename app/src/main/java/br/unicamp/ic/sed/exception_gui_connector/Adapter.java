package br.unicamp.ic.sed.exception_gui_connector;

import br.unicamp.ic.sed.global.datatypes.Move;

public class Adapter implements br.unicamp.ic.sed.exception.req.IGUIInterface{

	private IManager manager;

	Adapter (br.unicamp.ic.sed.exception_gui_connector.IManager manager) {
		this.manager = manager;
	}

	public void reportInvalidMove(Move move) {
		// NOTE that this is usually the provided interface
		// in this case the provided interface and required interface
		// are being used from the same place
		// but usually should be like gui.prov.IGUIInterface, because its the
		// component that will realize the interface;
		br.unicamp.ic.sed.engine.req.IGUIInterface guiinterface =
				(br.unicamp.ic.sed.engine.req.IGUIInterface) this.manager.getRequiredInterface("IGUIInterface");
		guiinterface.reportInvalidMove(move);
	}

}
