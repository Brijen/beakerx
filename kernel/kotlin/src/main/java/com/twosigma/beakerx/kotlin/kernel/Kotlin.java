/*
 *  Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.kotlin.kernel;

import static com.twosigma.beakerx.DefaultJVMVariables.IMPORTS;
import static com.twosigma.beakerx.kernel.Utils.uuid;

import com.twosigma.beakerx.AutotranslationServiceImpl;
import com.twosigma.beakerx.BeakerXCommRepository;
import com.twosigma.beakerx.CommRepository;
import com.twosigma.beakerx.NamespaceClient;
import com.twosigma.beakerx.evaluator.Evaluator;
import com.twosigma.beakerx.handler.KernelHandler;
import com.twosigma.beakerx.kernel.CacheFolderFactory;
import com.twosigma.beakerx.kernel.CloseKernelAction;
import com.twosigma.beakerx.kernel.CustomMagicCommandsEmptyImpl;
import com.twosigma.beakerx.kernel.Kernel;
import com.twosigma.beakerx.kernel.KernelConfigurationFile;
import com.twosigma.beakerx.kernel.EvaluatorParameters;
import com.twosigma.beakerx.kernel.KernelRunner;
import com.twosigma.beakerx.kernel.KernelSocketsFactory;
import com.twosigma.beakerx.kernel.KernelSocketsFactoryImpl;
import com.twosigma.beakerx.kernel.handler.CommOpenHandler;
import com.twosigma.beakerx.kotlin.comm.KotlinCommOpenHandler;
import com.twosigma.beakerx.kotlin.evaluator.KotlinEvaluator;
import com.twosigma.beakerx.kotlin.handler.KotlinKernelInfoHandler;
import com.twosigma.beakerx.message.Message;

import java.io.IOException;
import java.util.HashMap;


public class Kotlin extends Kernel {

  private Kotlin(final String id, final Evaluator evaluator, KernelSocketsFactory kernelSocketsFactory, CommRepository commRepository) {
    super(id, evaluator, kernelSocketsFactory, new CustomMagicCommandsEmptyImpl(), commRepository);
  }

  public Kotlin(final String id,
                final Evaluator evaluator,
                KernelSocketsFactory kernelSocketsFactory,
                CloseKernelAction closeKernelAction,
                CacheFolderFactory cacheFolderFactory,
                CommRepository commRepository) {
    super(id, evaluator, kernelSocketsFactory, closeKernelAction, cacheFolderFactory, new CustomMagicCommandsEmptyImpl(), commRepository);
  }

  @Override
  public CommOpenHandler getCommOpenHandler(Kernel kernel) {
    return new KotlinCommOpenHandler(kernel);
  }

  @Override
  public KernelHandler<Message> getKernelInfoHandler(Kernel kernel) {
    return new KotlinKernelInfoHandler(kernel);
  }

  public static EvaluatorParameters getKernelParameters() {
    HashMap<String, Object> kernelParameters = new HashMap<>();
    kernelParameters.put(IMPORTS, new KotlinDefaultVariables().getImports());
    return new EvaluatorParameters(kernelParameters);
  }

  public static void main(final String[] args) {
    KernelRunner.run(() -> {
      String id = uuid();
      CommRepository commRepository = new BeakerXCommRepository();
      KernelConfigurationFile configurationFile = new KernelConfigurationFile(args);
      KernelSocketsFactoryImpl kernelSocketsFactory = new KernelSocketsFactoryImpl(
              configurationFile);
      KotlinEvaluator e = new KotlinEvaluator(id,
              id,
              getKernelParameters(),
              NamespaceClient.create(id, configurationFile, commRepository));
      return new Kotlin(id, e, kernelSocketsFactory, commRepository);
    });
  }

}
