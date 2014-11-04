package com.ccnt.cado.algorithm.scheduler;

import java.util.List;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.VM;

public class SchedulResultItem {
	VM vm; //虚拟机
	List<Deploy> units; //该虚拟机部署的单元
}

